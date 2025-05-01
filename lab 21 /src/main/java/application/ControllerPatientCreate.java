package application;

import application.model.Doctor;
import application.model.DoctorRepository;
import application.model.Patient;
import application.model.PatientRepository;
import application.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import view.PatientView;

@Controller
public class ControllerPatientCreate {

	@Autowired
	private SequenceService sequenceService;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@GetMapping("/patient/new")
	public String getNewPatientForm(Model model) {
		model.addAttribute("patient", new PatientView());
		return "patient_register";
	}

	@PostMapping("/patient/new")
	public String createPatient(PatientView patientView, Model model) {
		try {
			int doctorId = findDoctorId(patientView.getPrimaryName());

			if (doctorId == -1) {
				model.addAttribute("message", "Error: Doctor not found.");
				model.addAttribute("patient", patientView);
				return "patient_register";
			}

			// Generate the next unique ID for the patient using the SequenceService
			int id = sequenceService.getNextSequence("patient_sequence");

			// Create a new Patient entity and set its properties
			Patient patient = Patient.fromView(patientView);
			patient.setId(id);

			// Save the patient to the database using the repository
			patientRepository.save(patient);

			// Display patient data and the generated patient ID, and success message
			model.addAttribute("message", "Registration successful.");
			model.addAttribute("patient", patient);
			return "patient_show";
		} catch (Exception e) {
			model.addAttribute("message", "Error: " + e.getMessage());
			model.addAttribute("patient", patientView);
			return "patient_register";
		}
	}

	@GetMapping("/patient/edit")
	public String getSearchForm(Model model) {
		model.addAttribute("patient", new PatientView());
		return "patient_get";
	}

	@PostMapping("/patient/show")
	public String showPatient(PatientView patientView, Model model) {
		try {
			// Find the patient by ID and last name using the repository
			Patient patient = patientRepository.findByIdAndLastName(patientView.getId(), patientView.getLastName());

			if (patient != null) {
				// Set the patient view properties from the retrieved patient entity
				model.addAttribute("patient", patient);
				return "patient_show";
			} else {
				model.addAttribute("message", "Patient not found.");
				model.addAttribute("patient", patientView);
				return "patient_get";
			}
		} catch (Exception e) {
			model.addAttribute("message", "Error: " + e.getMessage());
			model.addAttribute("patient", patientView);
			return "patient_get";
		}
	}

	private int findDoctorId(String lastName) {
		Doctor doctor = doctorRepository.findByLastName(lastName);
		return doctor != null ? doctor.getId() : -1;
	}
}
