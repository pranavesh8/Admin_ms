package com.central.game.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.central.game.entity.Admin;
public interface AdminRepository extends MongoRepository<Admin, String>{
	Admin findByUsername(String username);
    Admin findByEmail(String email);

}
