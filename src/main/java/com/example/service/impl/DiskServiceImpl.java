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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Service
public class DiskServiceImpl implements DiskService {
    private final OkHttpClient client = new OkHttpClient();
    private final Environment environment;

    @Value("${yandex.disk.file.path}")
    private String filePath;

    @Value("${yandex.disk.api.token}")
    private String apiToken;

    public DiskServiceImpl(Environment environment) {
        this.environment = environment;
    }

    //Получение URL для загрузки файла
    @Override
    public String upload() {
        try {
            File file = new File(filePath);
            String diskResource = environment.getProperty("yandex.disk.url.for.upload");
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
                    String makePublicURL = environment.getProperty("yandex.disk.url.for.make.file.public");
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
                            handleApiErrors(makePublicResponse);
                        } catch (RuntimeException exception) {
                            exception.printStackTrace();
                            return exception.getMessage();
                        }
                    }

                } else {
                    try {
                        handleApiErrors(uploadResponse);
                    } catch (RuntimeException exception) {
                        exception.printStackTrace();
                        return exception.getMessage();
                    }
                }

            } else {
                try {
                    handleApiErrors(response);
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

    private void handleApiErrors(Response response) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        HttpErrorDTO httpErrorDTO = mapper.readValue(response.body().string(), HttpErrorDTO.class);
        switch (response.code()) {
            case 400 -> throw new IncorrectDataException(httpErrorDTO.getMessage());
            case 401 -> throw new NotAuthorizedException(httpErrorDTO.getMessage());
            case 403 -> throw new NotAvailableAPIException(httpErrorDTO.getMessage());
            case 404 -> throw new NotFoundException(httpErrorDTO.getMessage() + " " + httpErrorDTO.getError());
            case 406 -> throw new NotAcceptableException(httpErrorDTO.getMessage());
            case 409 -> throw new ConflictException(httpErrorDTO.getMessage());
            case 413 -> throw new RequestEntityTooLargeException(httpErrorDTO.getMessage());
            case 423 -> throw new LockedException(httpErrorDTO.getMessage());
            case 429 -> throw new TooManyRequestsException(httpErrorDTO.getMessage());
            case 503 -> throw new ServiceUnavailableException(httpErrorDTO.getMessage());
            case 507 -> throw new InsufficientStorageException(httpErrorDTO.getMessage());
        }
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
