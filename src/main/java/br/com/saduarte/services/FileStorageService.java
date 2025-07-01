package br.com.saduarte.services;

import br.com.saduarte.config.FileStorageConfig;
import br.com.saduarte.controllers.FileController;
import br.com.saduarte.exception.FileNotFoundException;
import br.com.saduarte.exception.FileStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageConfig fileStorageConfig) {

        Path path = Paths.get(fileStorageConfig.getUploadDir()).toAbsolutePath().normalize();

        this.fileStorageLocation = path;

        try {
            logger.info("Criando diretorio");
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e){
            logger.error("Não pode criar o diretorio");
            throw new FileStorageException("Não pode criar o diretorio onde os arquivos estariam salvos",e);
        }
    }

    public String storeFile(MultipartFile file){

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {

            if (fileName.contains("..")) {
                logger.error("Nome inválido");
                throw  new FileStorageException("Desculpe! Nome do aquivo contém caminho inválido " +fileName);
            }
            logger.info("Salvando arquivo");

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;

        } catch (Exception e){
            logger.error("não foi possível armazenar o arquivo");
            throw  new FileStorageException("não foi possível armazenar o arquivo " + fileName + ". Porfavortente novamente", e);
        }

    }

    public Resource loadFileAsResource(String fileName){

        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()){
                return  resource;
            } else {
                logger.error("Arquivo não encontrado " +fileName);
                throw  new FileNotFoundException("Arquivo não encontrado " +fileName);
            }

        } catch (Exception e) {
            logger.error("Arquivo não encontrado " +fileName);
            throw  new FileNotFoundException("Arquivo não encontrado " +fileName, e);

        }


    }

}
