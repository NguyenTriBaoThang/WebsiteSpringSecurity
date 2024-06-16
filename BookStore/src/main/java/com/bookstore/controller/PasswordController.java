package com.bookstore.controller;

import com.bookstore.entity.User;
import org.springframework.ui.Model;
import com.bookstore.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class PasswordController {
    @Autowired
    private UserServices userService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "user/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("username") String username, Model model) {
        User user = userService.findByUsername(username);
        if (user == null) {
            model.addAttribute("error", "Không tìm thấy tài khoản với tên đăng nhập này.");
            return "user/forgot-password";
        }

        String token = UUID.randomUUID().toString();
        userService.updateResetPasswordToken(token, user.getEmail());

        return "redirect:/reset-password?token=" + token;
    }


    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        User user = userService.getByResetPasswordToken(token);
        if (user == null) {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            return "redirect:/forgot-password";
        }
        model.addAttribute("token", token);
        return "user/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token, @RequestParam("password") String password, Model model) {
        User user = userService.getByResetPasswordToken(token);
        if (user == null) {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            return "redirect:/forgot-password";
        }

        userService.updatePassword(user, password);

        model.addAttribute("message", "Mật khẩu của bạn đã được cập nhật thành công.");
        return "redirect:/login";
    }
}


