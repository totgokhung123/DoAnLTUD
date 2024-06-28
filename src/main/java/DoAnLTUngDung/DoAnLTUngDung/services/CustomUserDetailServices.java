package DoAnLTUngDung.DoAnLTUngDung.services;

import DoAnLTUngDung.DoAnLTUngDung.entity.CustomUserDetail;
import DoAnLTUngDung.DoAnLTUngDung.entity.User;
import DoAnLTUngDung.DoAnLTUngDung.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailServices implements UserDetailsService {
    @Autowired
    private IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomUserDetail(user, userRepository);
    }
}
