package com.example.service.impl;

import com.example.dto.GetUrlSuccessDTO;
import com.example.dto.HttpErrorDTO;
import com.example.dto.MakePublicAccessDTO;
import com.example.exceptions.*;
import com.example.service.DiskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiskServiceImpl implements DiskService {
    private final OkHttpClient client = new OkHttpClient();
    @Value("${yandex.disk.file.path}")
    private String filePath;

    @Value("${yandex.disk.api.token}")
    private String apiToken;

    //Получение URL для загрузки файла
    @Override
    public String upload() {
        try {
            File file = new File(filePath);
            String diskResource = "https://cloud-api.yandex.net/v1/disk/resources/upload";
            Request request = new Request.Builder()
                    .url(diskResource + "?path=" + file.getName())
                    .addHeader("Authorization", apiToken)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                // В случае успешного получения url достаём HREF(url) для файла и загружаем его
                ObjectMapper mapper = new ObjectMapper();
                GetUrlSuccessDTO getUrlSuccessDTO = mapper.readValue(response.body().string(), GetUrlSuccessDTO.class);
                URL url = new URL(getUrlSuccessDTO.getHref());
                Request uploadRequest = new Request.Builder()
                        .url(url)
                        .put(RequestBody.create(null, file))
                        .build();
                Response uploadResponse = client.newCall(uploadRequest).execute();

                if (uploadResponse.isSuccessful()) {
                    //В случае успешной загрузки файла делаем его публичным для больших возможностей взаимодействия с файлом
                    String makePublicURL = "https://cloud-api.yandex.net/v1/disk/resources/publish\n";
                    Request makePublicRequest = new Request.Builder()
                            .url(makePublicURL + "?path=" + "disk%3A%2F" + file.getName())
                            .addHeader("Authorization", apiToken)
                            .put(RequestBody.create(null, new byte[0]))
                            .build();
                    Response makePublicResponse = client.newCall(makePublicRequest).execute();
                    System.out.println("Make public response: " + makePublicResponse.body().string());

                    if (makePublicResponse.isSuccessful()) {
                        ObjectMapper publicMapper = new ObjectMapper();
                        MakePublicAccessDTO makePublicAccessDTO = publicMapper.readValue(makePublicResponse.body().string(), MakePublicAccessDTO.class);
                        return makePublicAccessDTO.getHref();
                    } else {
                        try {
                            getApiError(makePublicResponse);
                        } catch (RuntimeException exception) {
                            exception.printStackTrace();
                            return exception.getMessage();
                        }
                    }

                } else {
                    try {
                        getApiError(uploadResponse);
                    } catch (RuntimeException exception) {
                        exception.printStackTrace();
                        return exception.getMessage();
                    }
                }

            } else {
                try {
                    getApiError(response);
                } catch (RuntimeException exception) {
                    exception.printStackTrace();
                    return exception.getMessage();
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            return exception.getMessage();
        }

        return null;
    }

    private void getApiError(Response response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        HttpErrorDTO httpErrorDTO = mapper.readValue(response.body().string(), HttpErrorDTO.class);
        throw new DiskException(httpErrorDTO.getMessage());
    }

    // Перманентное удаление файла с диска (удаление не в корзину, а полное)
    @Override
    public String delete() {
        try {
            String deleteResource = "https://cloud-api.yandex.net/v1/disk/resources";
            String fileRef = "disk%3A%2FtestFile.txt";

            Request request = new Request.Builder()
                    .url(deleteResource + "?path=" + fileRef + "&permanently=true")
                    .addHeader("Authorization", apiToken)
                    .delete()
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException exception) {
            exception.printStackTrace();
            return exception.getMessage();
        }
    }

}
