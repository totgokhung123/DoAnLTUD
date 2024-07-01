package DoAnLTUngDung.DoAnLTUngDung.services;

import DoAnLTUngDung.DoAnLTUngDung.entity.Category;
import DoAnLTUngDung.DoAnLTUngDung.entity.User;
import DoAnLTUngDung.DoAnLTUngDung.repository.IRoleRepository;
import DoAnLTUngDung.DoAnLTUngDung.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServices {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IRoleRepository rolePepository;
    public void save(User user) {

        userRepository.save(user);
        Long userId = userRepository.getUserIdByUsername(user.getUsername());
        Long roleId = rolePepository.getRoleIdByName("USER");
        if(roleId != 0 && userId != 0){
            userRepository.addRoleToUser(userId,roleId);
        }
    }
    public List<User> getAllusers () {
        return userRepository.findAll();
    }
    public User getUsersById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User saveUsers(User user) {
        return userRepository.save(user);
    }

    public void deleteUsers(Long id) {
        userRepository.deleteById(id);
    }

    public User edit(User editedUser) {
        User existingUser = userRepository.findById(editedUser.getId()).orElse(null);
        if (existingUser != null) {
            existingUser.setUsername(editedUser.getUsername());
            existingUser.setPassword(new BCryptPasswordEncoder().encode(editedUser.getPassword()));
            existingUser.setEmail(editedUser.getEmail());
            existingUser.setName(editedUser.getName());
            return userRepository.save(existingUser);
        }
        return null;
    }
}