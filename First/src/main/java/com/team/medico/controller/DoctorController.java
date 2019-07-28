package com.team.medico.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.team.medico.model.AppointmentBooking;
import com.team.medico.model.Doctor;
import com.team.medico.model.PreferredLanguage;
import com.team.medico.model.Timeslot;
import com.team.medico.model.UploadedFile;
import com.team.medico.model.User;
import com.team.medico.service.EmailServiceImple;
import com.team.medico.service.MedicoService;

@Controller
public class DoctorController {
	
	@Autowired
	private MedicoService medService;
	
	@Autowired
	public EmailServiceImple emailService;
	
	//doctor profile
	@RequestMapping(value="/welcomeDoctor")
	public String welcomeDoctor(ModelMap model,HttpSession session) { //redirecting to doctor
		User user = (User) session.getAttribute("user");
		Doctor doctor = medService.doctorByEmailId(user.getEmailId());
		if(doctor.getStatus().equals("Pending")) { //checking the approval status
			return "pending";
		}
		List<AppointmentBooking> bookedAppList = medService.getBookedAppointmentOfDoctor(user.getEmailId());
		session.setAttribute("bookedAppList", bookedAppList);
		return "doctor";
	}
		
	
	//ajax call for login page
	@RequestMapping("/timeElapse")
		@ResponseBody
		public String getEmail(@RequestParam String slotIdString) {
			int slotId = Integer.parseInt(slotIdString);
			Timeslot timeslot = medService.getTimeSlotById(slotId); //we get slot id from the ajax call
			if(timeslot!=null) {
				//System.out.println(timeslot.getStartTime());
				
				Calendar cal = Calendar.getInstance();
		        Date date=cal.getTime();
		        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		        String later=dateFormat.format(date);
		        LocalTime currentTime = new LocalTime(later);
		        LocalTime earlierTable = new LocalTime(timeslot.getStartTime());
		        if(earlierTable.compareTo(currentTime)<0) {
		        	return "Appointment Active";
		        }
			}
			return "";
		}
	
	//video calling page
	@RequestMapping(value="/video")
	public String helloSuccess() { //redirecting to doctor
		return "video";
	}
	
	
	
	
		
		@RequestMapping(value="/signUpDoctor")
		public String signUpDoctor(ModelMap model) {
			model.put("user", new User());
			model.put("doctor",new Doctor());
			model.put("uploadFile",new UploadedFile());
			return "sign-up-doctor";
		}
		
		//after submit of doctor reg
		@RequestMapping(value="/saveDoctor")
		public String saveDoctor(@RequestParam(name = "prefLanguage")List<String> pl,UploadedFile uploadedFile, Doctor doctor,User user,ModelMap model) {
			
			if(medService.doctorByEmailId(doctor.getEmailId())!=null) {
				return "sign-up-doctor";
			}
			 InputStream inputStreamL = null;    
			  OutputStream outputStreamL = null;
			  InputStream inputStreamD = null;    
			  OutputStream outputStreamD = null;
			    
			  MultipartFile fileLicense = uploadedFile.getFileLicense();    
			  MultipartFile fileDegree = uploadedFile.getFileDegree();    
			  
			    
			  String fileNameLicense = fileLicense.getOriginalFilename();
			  String fileNameDegree = fileDegree.getOriginalFilename();
			  String pathLicense = "D:\\Medico\\"+user.getContactNo().toString()+"-license-"+fileNameLicense;
			  String pathDegree = "D:\\Medico\\"+user.getContactNo().toString()+"-degree-"+fileNameDegree;
			  doctor.setDegreeImg(pathDegree);
			  doctor.setLicenseImg(pathLicense);
			
			try {    
				   inputStreamL = fileLicense.getInputStream();
				   inputStreamD = fileDegree.getInputStream();
				    
				   File newFileL = new File(pathLicense);
				   File newFileD = new File(pathDegree);
				   if (!newFileL.exists()) {    
				    newFileL.createNewFile();    
				   }  
				   if (!newFileD.exists()) {    
					    newFileD.createNewFile();    
				   } 
				   outputStreamL = new FileOutputStream(newFileL);    
				   outputStreamD = new FileOutputStream(newFileD);    
				   int read = 0;    
				   byte[] bytes = new byte[1024];    
				    
				   while ((read = inputStreamL.read(bytes)) != -1) {    
				    outputStreamL.write(bytes, 0, read);    
				   } 
				   while ((read = inputStreamD.read(bytes)) != -1) {    
					    outputStreamD.write(bytes, 0, read);    
					   } 
				  } catch (IOException e) {    
				   // TODO Auto-generated catch block    
				   e.printStackTrace();    
				  }    
			
			Set<PreferredLanguage> preferredLanguage = new HashSet<PreferredLanguage>();
			for(String items : pl){
				preferredLanguage.add(medService.getLanguage(items));//fecthing from database
			}
			user.setPreferredLanguage(preferredLanguage);//adding to particular doctor
			user.setUserType("doctor");
			user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
			medService.insertDoctor(doctor,user);
			emailService.sendSimpleMessage(user.getEmailId(), "Welcome To Medico", "Thank you for registering");
			
			model.put("user", new User());
			model.put("doctor",new Doctor());
			
			
			return "login";

		}
}
