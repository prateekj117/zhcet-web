package in.ac.amu.zhcet.controller.login;

import in.ac.amu.zhcet.data.model.token.PasswordResetToken;
import in.ac.amu.zhcet.service.token.PasswordResetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

import static in.ac.amu.zhcet.utils.Utils.getAppUrl;

@Slf4j
@Controller
public class ForgotPasswordController {

    private final PasswordResetService passwordResetService;

    @Autowired
    public ForgotPasswordController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/login/forgot_password")
    public String getForgetPassword() {
        return "forgot_password";
    }

    @PostMapping("/login/forgot_password")
    public String sendEmailLink(RedirectAttributes redirectAttributes, @RequestParam String email, HttpServletRequest request) {
        try {
            PasswordResetToken token = passwordResetService.generate(email);
            passwordResetService.sendMail(getAppUrl(request), token);
            redirectAttributes.addFlashAttribute("reset_link_sent", true);
        } catch(UsernameNotFoundException e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login/forgot_password";
        }

        return "redirect:/login";
    }

}
