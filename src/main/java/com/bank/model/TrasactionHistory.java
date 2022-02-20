package com.bank.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data

@Entity
@Table(name = "trasactionhistory")
public class TrasactionHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private long id;
	@Column
	private Timestamp activitydate;
	@Column
	private int amount;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "transactiontype_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)@JsonIgnore
	private TransactionType transactiontype;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "users_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	private Users user;

}
