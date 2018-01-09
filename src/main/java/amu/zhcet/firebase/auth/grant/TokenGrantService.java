package amu.zhcet.firebase.auth.grant;

import amu.zhcet.core.auth.Auditor;
import amu.zhcet.core.auth.CustomUser;
import amu.zhcet.firebase.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
class TokenGrantService {

    private static final UserToken UNAUTHENTICATED = new UserToken();

    private final FirebaseService firebaseService;
    private final ModelMapper modelMapper;

    @Autowired
    public TokenGrantService(FirebaseService firebaseService, ModelMapper modelMapper) {
        this.firebaseService = firebaseService;
        this.modelMapper = modelMapper;
    }

    /**
     * Generates custom firebase token for authenticated user
     * Note: Only to be called from an authenticated endpoint
     * @return UserToken
     */
    public UserToken generateToken() {
        if (!firebaseService.canProceed())
            return null;

        try {
            Optional<CustomUser> userOptional = Auditor.getLoggedInUser();
            if (!userOptional.isPresent()) return UNAUTHENTICATED;
            CustomUser user = userOptional.get();
            Map<String, Object> claims = new HashMap<>();
            claims.put("type", user.getType().toString());
            claims.put("department", user.getDepartment().getName());
            String token = FirebaseAuth.getInstance().createCustomTokenAsync(user.getUsername(), claims).get();
            return fromUser(user, token);
        } catch (InterruptedException | ExecutionException e) {
            return UNAUTHENTICATED;
        }
    }

    private UserToken fromUser(CustomUser user, String token) {
        if (user == null) {
            return UNAUTHENTICATED;
        }

        UserToken information = modelMapper.map(user, UserToken.class);
        information.setToken(token);
        information.setAuthenticated(true);
        return information;
    }

}
