package com.vapor.vapor.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//TODO: ssanchez - se podría cambiar el nobmre a UsuarioRegisterDTO
public class UserRegisterDTO {
    private String email;
    private String password;
    private Date fechaNacimiento;
    private String sexo;
}