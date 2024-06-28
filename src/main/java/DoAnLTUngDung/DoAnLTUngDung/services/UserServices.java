package DoAnLTUngDung.DoAnLTUngDung.services;

import DoAnLTUngDung.DoAnLTUngDung.entity.User;
import DoAnLTUngDung.DoAnLTUngDung.repository.IRoleRepository;
import DoAnLTUngDung.DoAnLTUngDung.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}