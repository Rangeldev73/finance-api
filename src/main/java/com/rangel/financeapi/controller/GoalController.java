package com.rangel.financeapi.controller;

import com.rangel.financeapi.dto.GoalRequestDTO;
import com.rangel.financeapi.dto.GoalResponseDTO;
import com.rangel.financeapi.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<GoalResponseDTO> create(
            @Valid @RequestBody GoalRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.status(201)
                .body(goalService.createGoal(dto, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<GoalResponseDTO>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(goalService.getGoalsByUser(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponseDTO> getGoalById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(goalService.getGoalById(id, userDetails.getUsername())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails){
        goalService.deleteGoal(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponseDTO> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody GoalRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(goalService.updateGoal(id,dto,userDetails.getUsername()));
    }
}
