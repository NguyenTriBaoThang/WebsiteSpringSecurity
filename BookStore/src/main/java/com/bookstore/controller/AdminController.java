package com.bookstore.controller;

import com.bookstore.entity.Role;
import com.bookstore.services.UserServices;
import com.bookstore.repository.IUserRepository;
import com.bookstore.repository.IRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserServices userServices;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @GetMapping("/manage-roles")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showManageRolesPage(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        return "/user/manage-roles";
    }

    @PostMapping("/assign-role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String assignRoleToUser(@RequestParam String username, @RequestParam String roleName, RedirectAttributes redirectAttributes) {
        userServices.assignRoleToUser(username, roleName);
        if (username.equals("2180601452")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phân quyền thất bại vì đây là username admin mặc định!");
            return "redirect:/admin/manage-roles";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "Phân quyền thành công");
        return "redirect:/admin/manage-roles";
    }

    @GetMapping("/admin/roles-by-user")
    @ResponseBody
    public List<Role> getRolesByUsername(@RequestParam String username) {
        return userServices.getRolesByUsername(username);
    }
}