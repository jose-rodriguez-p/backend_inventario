/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package multiservicioRafael.invenatario.controladores;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import multiservicioRafael.invenatario.CodigoFuente.Sistema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author jose
 */
@RestController
@RequestMapping("/api/configuracion")
@CrossOrigin(origins = "*")
public class ControladorConfiguracion {

    @GetMapping("/roles/listar")
    public ResponseEntity<List<Map<String, Object>>> listarRoles() {
        try {
            List<Map<String, Object>> roles = Sistema.getInstancia().listarRoles();

            if (roles == null || roles.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/roles/crear")
    public ResponseEntity<Boolean> crearRol(@RequestBody Map<String, Object> nuevoRol) {
        try {
            boolean guardado = Sistema.getInstancia().agregarRol(nuevoRol);
            if (guardado) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.badRequest().body(false);
            }
        } catch (Exception e) {
            System.out.println("Error en controlador al guardar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}
