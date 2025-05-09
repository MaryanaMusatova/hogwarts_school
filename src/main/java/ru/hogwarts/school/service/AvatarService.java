package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    @Value("${avatar.cover.dir.path}")
    private String avatarDir;

    private final StudentService studentService;
    private final AvatarRepository avatarRepository;

    public AvatarService(StudentService studentService, AvatarRepository avatarRepository) {
        this.studentService = studentService;
        this.avatarRepository = avatarRepository;
        logger.info("AvatarService initialized");
    }

    public Page<Avatar> getAllAvatars(int page, int size) {
        logger.debug("Getting all avatars, page: {}, size: {}", page, size);
        Page<Avatar> avatars = avatarRepository.findAll(PageRequest.of(page, size));
        logger.debug("Found {} avatars", avatars.getTotalElements());
        return avatarRepository.findAll(PageRequest.of(page, size));
    }

    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("Starting avatar upload for student ID: {}", studentId);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл не может быть пустым");
        }

        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new IllegalArgumentException("Файл должен быть изображением");
        }

        Student student = studentService.getStudentById(studentId)
                .orElseThrow(() -> {
                    logger.error("Student not found with ID: {}", studentId);
                    return new RuntimeException("Студент не найден");
                });


        String extension = getExtension(Objects.requireNonNull(file.getOriginalFilename()));
        Path filePath = Path.of(avatarDir, studentId + "." + extension);
        logger.debug("Preparing to save avatar to: {}", filePath);

        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);


        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is);
             BufferedOutputStream bos = new BufferedOutputStream(os)) {
            bis.transferTo(bos);
            logger.debug("Avatar file saved successfully");
        } catch (IOException e) {
            logger.error("Failed to save avatar file", e);
            throw e;
        }

        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(new Avatar());
        logger.debug("Found existing avatar: {}", avatar.getId() != null);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());

        try {
            byte[] previewData = generateImagePreview(filePath);
            avatar.setData(previewData);
            logger.debug("Image preview generated successfully");
        } catch (IOException e) {
            logger.error("Failed to generate image preview", e);
            throw e;
        }

        Avatar savedAvatar = avatarRepository.save(avatar);
        logger.info("Avatar uploaded successfully for student ID: {}. Avatar ID: {}", studentId, savedAvatar.getId());
    }


    public Avatar findAvatar(Long studentId) {
        logger.debug("Looking for avatar by student ID: {}", studentId);
        return avatarRepository.findByStudentId(studentId)
                .orElseThrow(() -> {
                    logger.error("Avatar not found for student ID: {}", studentId);
                    return new RuntimeException("Аватар не найден");
                });
    }

    private byte[] generateImagePreview(Path filePath) throws IOException {
        logger.debug("Generating image preview for: {}", filePath);
        try (InputStream is = Files.newInputStream(filePath);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            BufferedImage originalImage = ImageIO.read(is);
            if (originalImage == null) {
                logger.error("Failed to read image file: {}", filePath);
                throw new IOException("Не удалось прочитать изображение");
            }

            int targetWidth = 100;
            int targetHeight = (int) ((double) originalImage.getHeight() / originalImage.getWidth() * targetWidth);
            logger.trace("Original dimensions: {}x{}, Preview dimensions: {}x{}",
                    originalImage.getWidth(), originalImage.getHeight(), targetWidth, targetHeight);


            BufferedImage preview = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = preview.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
            graphics.dispose();

            if (!ImageIO.write(preview, "jpg", baos)) {
                logger.error("Failed to generate preview image in JPG format");
                throw new IOException("Не удалось сгенерировать превью");
            }
            return baos.toByteArray();
        }

    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}