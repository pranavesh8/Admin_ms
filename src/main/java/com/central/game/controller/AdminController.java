package com.central.game.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.central.game.entity.Admin;
import com.central.game.entity.LoginRequest;
import com.central.game.entity.LoginResponse;
import com.central.game.repository.AdminRepository;
import java.util.List;
@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/save")
    public ResponseEntity<String> saveAdmin(@RequestBody Admin admin) {
        if (adminRepository.findByUsername(admin.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        }
        if (adminRepository.findByEmail(admin.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists.");
        }

       String encryptPasword = restTemplate.getForObject("http://ENCRYPTDECRYPT-MS/api/encrypt?encrypt="+admin.getPassword(),String.class);
       admin.setPassword(encryptPasword);
       adminRepository.save(admin);
       return ResponseEntity.ok("Admin saved successfully!");
    }

    @GetMapping("/list")
    public ResponseEntity<List<Admin>> getAdmins() {
        List<Admin> admins = adminRepository.findAll();
        return ResponseEntity.ok(admins);
    }

    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().equals("")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse("* Username must be required !", false));
        }
        else if(request.getPassword() == null || request.getPassword().equals("")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse("* Password must be required !", false));
        }
        else {
        	Admin admin = adminRepository.findByUsername(request.getUsername());
        	String encryptPassword = restTemplate.getForObject("http://ENCRYPTDECRYPT-MS/api/encrypt?encrypt="+request.getPassword(),String.class);
        	if (admin != null && !admin.getUsername().equals(request.getUsername())) {
        		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("* Invalid Username !", false));
            } else if(admin != null && !admin.getPassword().equals(encryptPassword)){
            	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("* Invalid Password !", false));
            } else if(admin != null && admin.getUsername().equals(request.getUsername()) && admin.getPassword().equals(encryptPassword)){
            	return ResponseEntity.ok(new LoginResponse("* Login Sucessful !", true));
            }
        	return ResponseEntity.ok(new LoginResponse("* Invalid Username !", true));
        }
    }
}

