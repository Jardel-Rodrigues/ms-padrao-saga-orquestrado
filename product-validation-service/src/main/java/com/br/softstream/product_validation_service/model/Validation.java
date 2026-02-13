package com.br.softstream.product_validation_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_validation")
@Entity(name = "tb_validation")
public class Validation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String orderId;
	
	@Column(nullable = false)
	private String transactionId;
	
	@Column(nullable = false)
	private boolean sucess;
	
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	private LocalDateTime updatedAt;
	
	@PrePersist
	public void prePersist() {
		var now  = LocalDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;
	}
	
	@PreUpdate
	public void preUpdate() {
		var now  = LocalDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;
	}
}
