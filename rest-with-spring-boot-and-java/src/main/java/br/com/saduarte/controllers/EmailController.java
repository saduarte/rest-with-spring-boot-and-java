package br.com.saduarte.controllers;

import br.com.saduarte.controllers.docs.EmailControllerDocs;
import br.com.saduarte.data.dto.request.EmailRequestDTO;
import br.com.saduarte.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/email/v1")
public class EmailController implements EmailControllerDocs {

    @Autowired
    private EmailService service;


    @PostMapping
    @Override
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO emailRequestDTO) {
        service.sendSimpleEmail(emailRequestDTO);
        return new ResponseEntity<>("e-Mail send with success!", HttpStatus.OK);
    }

    @PostMapping(value = "/withAttachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<String> sendEmailWithAttachment(@RequestParam("emailRequest")String emailRequest, @RequestParam("attachment") MultipartFile multipartFile) {
        service.setEmailWithAttachment(emailRequest,multipartFile);
        return  new ResponseEntity<>("e-Mail with attachment sent successfully!", HttpStatus.OK);
    }
}
