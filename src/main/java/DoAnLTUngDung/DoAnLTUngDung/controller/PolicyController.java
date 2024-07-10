package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.Policy;
import DoAnLTUngDung.DoAnLTUngDung.services.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PolicyController {
    @Autowired
    private PolicyService policyService;

    @GetMapping("/policy/add")
    public String showAddForm(Model model) {
        model.addAttribute("policy", new Policy());
        return "ADMIN/addPolicy";
    }


    @PostMapping("/policy/add")
    public String addCategory(@Valid Policy policy, BindingResult result) {
        if (result.hasErrors()) {
            return "ADMIN/addPolicy";
        }
        policyService.addPolicy(policy);
        return "redirect:/policy";
    }

    @GetMapping("/policy")
    public String listPolicy(Model model) {
        List<Policy> policies = policyService.getAllPolicies();
        model.addAttribute("policies", policies);
        return "ADMIN/DSPolicy";
    }

    @GetMapping("/policy/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Policy policy = policyService
                .getPolicyById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid policy Id:" + id));
        model.addAttribute("policy", policy);
        return "ADMIN/editPolicy";
    }
    // POST request to update category
    @PostMapping("/policy/update/{id}")
    public String updatePolicy(@PathVariable("id") Long id, @Valid Policy policy, BindingResult result, Model model) {
        if (result.hasErrors()) {
            policy.setId(id);
            return "ADMIN/editPolicy";
        }

        Policy existingPolicy;
        try {
            existingPolicy = policyService.getPolicyById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid policy Id:" + id));
        } catch (Exception e) {
            e.printStackTrace();
            return "ADMIN/editPolicy"; // Trở lại form nếu có lỗi xảy ra
        }

        try {
            existingPolicy.setType(policy.getType());
            existingPolicy.setDescription(policy.getDescription());
            policyService.updatePolicy(existingPolicy);
        } catch (Exception e) {
            e.printStackTrace();
            return "ADMIN/editPolicy";
        }

        model.addAttribute("policies", policyService.getAllPolicies());
        return "redirect:/policy";
    }

    @GetMapping("/policy/delete/{id}")
    public String deletePolicy(@PathVariable("id") Long id, Model model) {
        Policy policy = policyService.getPolicyById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        policyService.deletePolicyById(id);
        model.addAttribute("policies", policyService.getAllPolicies());
        return "redirect:/policy";
    }
}