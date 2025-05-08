package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarService {

    @Value("${avatar.cover.dir.path}")
    private String avatarDir;

    private final StudentService studentService;
    private final AvatarRepository avatarRepository;

    public AvatarService(StudentService studentService, AvatarRepository avatarRepository) {
        this.studentService = studentService;
        this.avatarRepository = avatarRepository;
    }

    public Page<Avatar> getAllAvatars(int page, int size) {
        return avatarRepository.findAll(PageRequest.of(page, size));
    }
    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        // Проверка файла
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл не может быть пустым");
        }

        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new IllegalArgumentException("Файл должен быть изображением");
        }

        Student student = studentService.getStudentById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));

        // Подготовка пути для сохранения
        String extension = getExtension(Objects.requireNonNull(file.getOriginalFilename()));
        Path filePath = Path.of(avatarDir, studentId + "." + extension);

        // Создание директории, если не существует
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        // Сохранение оригинального файла
        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is);
             BufferedOutputStream bos = new BufferedOutputStream(os)) {
            bis.transferTo(bos);
        }

        // Создание или обновление аватара
        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(new Avatar());

        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());

        // Генерация и сохранение превью//для БД
        byte[] previewData = generateImagePreview(filePath);
        avatar.setData(previewData);

        avatarRepository.save(avatar);
    }

    public Avatar findAvatar(Long studentId) {
        return avatarRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Аватар не найден"));
    }

    private byte[] generateImagePreview(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            BufferedImage originalImage = ImageIO.read(is);
            if (originalImage == null) {
                throw new IOException("Не удалось прочитать изображение");
            }

            // Вычисление размеров превью с сохранением пропорций
            int targetWidth = 100;
            int targetHeight = (int) ((double) originalImage.getHeight() / originalImage.getWidth() * targetWidth);

            // Создание уменьшенной копии
            BufferedImage preview = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = preview.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
            graphics.dispose();

            // Конвертация в byte[]
            if (!ImageIO.write(preview, "jpg", baos)) {
                throw new IOException("Не удалось сгенерировать превью");
            }
            return baos.toByteArray();
        }
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}