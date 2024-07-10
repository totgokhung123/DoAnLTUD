package DoAnLTUngDung.DoAnLTUngDung.services;

import DoAnLTUngDung.DoAnLTUngDung.entity.Policy;
import DoAnLTUngDung.DoAnLTUngDung.repository.IPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class PolicyService {
    private final IPolicyRepository policyRepository;

    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    public Optional<Policy> getPolicyById( Long id) {
        return policyRepository.findById(id);
    }

    public void addPolicy(Policy policy) {
        policyRepository.save(policy);
    }

    public void updatePolicy(@NotNull Policy policy) {
        Policy existingPolicy = policyRepository.findById(policy.getId())
                .orElseThrow(() -> new IllegalStateException("Policy with ID " +
                        policy.getId() + " does not exist."));
        existingPolicy.setType(policy.getType());
        policyRepository.save(existingPolicy);
    }

    public void deletePolicyById(Long id) {
        if (!policyRepository.existsById(id)) {
            throw new IllegalStateException("Policy with ID " + id + " does not exist.");
        }
        policyRepository.deleteById(id);
    }
}