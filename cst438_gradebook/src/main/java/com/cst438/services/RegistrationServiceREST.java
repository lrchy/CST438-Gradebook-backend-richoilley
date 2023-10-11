package com.cst438.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.FinalGradeDTO;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Enrollment;

@Service
@ConditionalOnProperty(prefix = "registration", name = "service", havingValue = "rest")
@RestController
public class RegistrationServiceREST implements RegistrationService {

	
	RestTemplate restTemplate = new RestTemplate();
	
	@Value("${registration.url}") 
	String registration_url;
	
	public RegistrationServiceREST() {
		System.out.println("REST registration service ");
	}
	
	@Override
	public void sendFinalGrades(int course_id , FinalGradeDTO[] grades) { 
		
		//construct the endpoint URL for sending final grades :
		String endpoint = registration_url + "/" + course_id + "/finalgrades";
		
		//send the grades using RestTemplate's PUT method (URL,Object):
		restTemplate.put(endpoint, grades);
		
	}
	
	@Autowired
	CourseRepository courseRepository;

	@Autowired
	EnrollmentRepository enrollmentRepository;

	
	/*
	 * endpoint used by registration service to add an enrollment to an existing
	 * course.
	 */
	@PostMapping("/enrollment")
	@Transactional
	public EnrollmentDTO addEnrollment(@RequestBody EnrollmentDTO enrollmentDTO) {
		
		// Receive message from registration service to enroll a student into a course.
		
		System.out.println("GradeBook addEnrollment "+enrollmentDTO);
		
		//Check if course exists:
		Course course = courseRepository.findById(enrollmentDTO.courseId()).orElse(null);
		
		if (course==null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not found");
		}
		
		//create and save the enrollment
		Enrollment enrollment = new Enrollment();
		enrollment.setCourse(course);
		enrollment.setStudentEmail(enrollmentDTO.studentEmail());
		enrollment.setStudentName(enrollmentDTO.studentName());
		
		enrollment = enrollmentRepository.save(enrollment);
		
		return new EnrollmentDTO(enrollment.getId(), enrollment.getStudentEmail(), enrollment.getStudentName(), course.getCourse_id());
		
	}

}
