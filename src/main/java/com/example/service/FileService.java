package com.example.service;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Service
public class FileService {
    private final OkHttpClient client = new OkHttpClient();
    private final String filePath = "C:\\Users\\avdee\\IdeaProjects\\interaction-with-Yandex-API\\src\\main\\java\\com\\example\\data\\testFIle.txt";
    private final String apiToken = "y0_AgAAAAAaX4_0AADLWwAAAADwN1ly1xE3GqkBR3q05NdGNp4XBN7y2d4";

    //Получение URL для загрузки файла
    public String getURL() throws IOException {
        File file = new File(filePath);

        String diskResource = "https://cloud-api.yandex.net/v1/disk/resources/upload";
        Request request = new Request.Builder()
                .url(diskResource + "?path=" + file.getName())
                .addHeader("Authorization", apiToken)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    //TODO: Парсить JSON из getURL() для того, чтобы не приходилось в ручную записывать url для загрузки файла в метод uploadFile()
    public String uploadFile() throws IOException {
        URL url = new URL("https://uploader15o.disk.yandex.net:443/upload-target/20231028T092729.124.utd.2mkja4llvpftk0bfzpq7oecof-k15o.6147780");
        File file = new File(filePath);
        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(null, file))
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    // Перманентное удаление файла с диска (удаление не в корзину, а полное)
    public String deleteFile() throws IOException {
        String deleteResource = "https://cloud-api.yandex.net/v1/disk/resources";
        String fileRef = "disk%3A%2FtestFIle.txt";

        Request request = new Request.Builder()
                .url(deleteResource + "?path=" + fileRef + "&permanently=true")
                .addHeader("Authorization", apiToken)
                .delete()
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    // Генерация ссылки, по которой файл будет доступен не только владельцу
    public String makeFilePublic() throws IOException {
        String resource = "https://cloud-api.yandex.net/v1/disk/resources/publish";
        File file = new File(filePath);

        Request request = new Request.Builder()
                .url(resource + "?path=" + file.getName())
                .addHeader("Authorization", apiToken)
                .put(RequestBody.create(null, new byte[0]))
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
