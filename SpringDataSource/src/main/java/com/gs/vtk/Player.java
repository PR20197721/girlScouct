package com.gs.vtk;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
 
@Entity
@Table(name = "PLAYER")
public class Player {
 
    @Id
    @GeneratedValue
    @Column(name = "PLAYER_ID")
    private int playerId;
 
    @Column(name= "NAME")
    private String name;
 
    @Column(name= "AGE")
    private int age;

    
    
    
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
    
    
    
}