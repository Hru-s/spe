package iiitb.chat.service;

import iiitb.chat.dto.LoginRequest;
//import com.prashantjain.esdtestingprogram.exception.CustomerNotFoundException;
//import iiitb.chat.entity.Credentials;
import iiitb.chat.helper.EncryptionService;
import iiitb.chat.helper.JWTHelper;
//import iiitb.chat.mapper.CredentialsMapper;
//import iiitb.chat.repo.CredentialsRepo;
import iiitb.chat.repo.EmployeeRepo;
import iiitb.chat.entity.Employees;
import iiitb.chat.repo.EmployeeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class LoginService {
    //private final CredentialsRepo credentialsRepo;
    //private final CredentialsMapper credentialsMapper;
    private final EncryptionService encryptionService;
    private final JWTHelper jwtHelper;
    private final EmployeeRepo employeeRepo;

    public String login(LoginRequest request) {
        Employees employees = employeeRepo.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("email doesnot exist"));
        System.out.println("password ="+ employees.getPassword());
        String raw = request.password();
        String encoded = encryptionService.encode(raw);
        System.out.println("Encrypted form of request password: " + encoded);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("Test: " + encoder.matches("password", "$2a$10$gskLMqEXZWqVqsOmOBQe6.Lj7OE5U3ssOnxXWvVhTPHpxhQ59sG1e"));

        if(!encryptionService.validates(request.password(), employees.getPassword())) {
            return "Wrong Password or Email";
        }

        return jwtHelper.generateToken(String.valueOf(employees.getId()));
    }

//    public String createUser(LoginRequest request) {
//        Credentials credentials = credentialsMapper.toCreds(request);
//        credentials.setPassword(encryptionService.encode(credentials.getPassword()));
//        credentialsRepo.save(credentials);
//        return "Customer Created Successfully";
//    }
}
