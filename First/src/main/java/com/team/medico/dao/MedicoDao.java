package com.team.medico.dao;

import java.util.List;

import com.team.medico.model.Admin;
import com.team.medico.model.Doctor;
import com.team.medico.model.History;
import com.team.medico.model.Patient;
import com.team.medico.model.PreferredLanguage;
import com.team.medico.model.User;

public interface MedicoDao {	
	public void saveUser(final User user);
	public void savePatient(final Patient patient);
	public boolean validateUser(final User user);
	public User getUserByEmailId(String emailId);
	public boolean aadharExist(double aadhar);
	public boolean contactExist(String contact);
	public PreferredLanguage getLanguageById(String languageId);
	public void saveHistory(History history);
	public void insertDoctor(Doctor doctor,User user);
	public void saveAdmin(Admin admin);
	public Doctor getDoctorByEmailId(String emailId);
	public Patient getPatientByEmailId(String emailId);
	public List<Doctor> getApprovedDoctor();
}

