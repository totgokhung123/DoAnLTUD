package DoAnLTUngDung.DoAnLTUngDung.services;

import DoAnLTUngDung.DoAnLTUngDung.entity.Role;
import DoAnLTUngDung.DoAnLTUngDung.entity.User;
import DoAnLTUngDung.DoAnLTUngDung.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private IUserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // Get the attributes from the OAuth2User
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Get the user information from the attributes
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // Check if the user already exists in the database
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;


        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            // If the user doesn't exist, create a new user
            user = new User();
            user.setEmail(email);
            user.setUsername(name);
            user.setPassword(email);
            user.setName(name);
            Role role = new Role();
            role.setName("USER");
            user.setRoles(Collections.singletonList(role));

            userRepository.save(user);
        }

        // Set authorities and return the user
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        if (oAuth2User instanceof OidcUser) {
            return new DefaultOidcUser(authorities, ((OidcUser) oAuth2User).getIdToken(), "email");
        } else {
            return new org.springframework.security.oauth2.core.user.DefaultOAuth2User(authorities, attributes, "email");
        }
    }
}