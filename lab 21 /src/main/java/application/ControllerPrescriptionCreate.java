package application;


import application.model.*;
import application.service.SequenceService;
import org.bson.codecs.jsr310.LocalDateCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import view.PrescriptionView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@Controller
public class ControllerPrescriptionCreate {



//	@Autowired
//	private DrugRepository drugRepository;
//
//
//	@Autowired
//	private PrescriptionRepository prescriptionRepository;
@Autowired
private DoctorRepository doctorRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private DrugRepository drugRepository;


	@Autowired
	private PrescriptionRepository prescriptionRepository;

	@Autowired
	private PharmacyRepository pharmacyRepository;

	@Autowired
	SequenceService sequence;

	/*
	 * Doctor requests blank form for new prescription.
	 */
	@GetMapping("/prescription/new")
	public String getPrescriptionForm(Model model) {
		model.addAttribute("prescription", new PrescriptionView());
		return "prescription_create";
	}
	@PostMapping("/prescription")
	public String createPrescription(PrescriptionView p, Model model) {
		// Validate patient, doctor, and drug information (TODO)

		// Insert prescription into the database
		try {

			// Find the drug by name
			Drug drug = drugRepository.findByName(p.getDrugName());
			if (drug == null) {
				// Drug not found, handle this case
				model.addAttribute("message", "Error: Drug not found.");
				model.addAttribute("prescription", p);
				return "prescription_create";
			}

			// Create a new Prescription object
			Prescription prescription = new Prescription();
			// Generate the next unique ID for the patient using the SequenceService
			int id = sequence.getNextSequence("prescription_sequence");
			prescription.setRxid(id);
			p.setRxid(id);
			prescription.setDrugName(p.getDrugName());
			prescription.setQuantity(p.getQuantity());
			prescription.setPatientId(p.getPatientId());
			prescription.setDoctorId(p.getDoctorId());
			// Obtain today's date
			LocalDate today = LocalDate.now();
			prescription.setDateCreated(today.toString());
			prescription.setRefills(p.getRefillsRemaining());

			// Save the prescription to the database
			prescriptionRepository.save(prescription);

			// Prescription created successfully, return to prescription_show template
			model.addAttribute("message", "Prescription created.");
			model.addAttribute("prescription", p);
			return "prescription_show";
		} catch (Exception e) {
			// Error occurred, handle it and return to prescription_create template
			model.addAttribute("message", "Error: Prescription creation failed. " + e.getMessage());
			model.addAttribute("prescription", p);
			return "prescription_create";
		}
	}

@GetMapping("/prescription/fill")
public String getPrescriptionFillForm(Model model) {
	model.addAttribute("prescription", new PrescriptionView());
	return "prescription_fill";
}
@PostMapping("/prescription/fill")
	public String fillPrescription(PrescriptionView p, Model model) {
		/*
		 * valid pharmacy name and address, get pharmacy id and phone
		 */
		if (pharmacyRepository.findByNameAndAddress(p.getPharmacyName(), p.getPharmacyAddress()) == null) {
			model.addAttribute("message", "Pharmacy not found");
			model.addAttribute("prescription", p);
			return "prescription_fill";
		}
		;

		//Insert the prescription to a collection
		//Check if rx exists and then determine whether to fill the rx or not
		Optional<Prescription> optionalPrescription = prescriptionRepository.findById(p.getRxid());
		if (optionalPrescription.isPresent()) {
			Prescription prescription = optionalPrescription.get();
			//Get # of refills already done and determine whether to fill an rx
			if (prescription.getFills().size() > prescription.getRefills()) {
				model.addAttribute("message", "This prescription cannot be refilled.");
				model.addAttribute("prescription", p);
				return "prescription_fill";
			} else {
				//Since there are fills insert the prescription to a collection


				//add presciption fill to prescription.getFills(), it's an arrayList
				//of FillRequest

				//Prescription fill is an inner class of Prescription so the method
				//has to be written while referencing the inner class

				//Set view values that are not Prescription

				//Start with setting dr
				int drID = prescription.getDoctorId();
				p.setDoctorId(drID);
				Doctor dr = doctorRepository.findById(drID);
				p.setDoctorFirstName(dr.getFirstName());
				p.setDoctorLastName(dr.getLastName());


				//set patient
				int ptID = prescription.getPatientId();
				Optional<Patient> optionalPatient = patientRepository.findById(ptID);
				Patient pt = null;
				if (optionalPatient.isPresent()) {
					pt = optionalPatient.get();
				}
				if (pt != null) {
					p.setPatientFirstName(pt.getFirstName());
					p.setPatientLastName(pt.getLastName());
					p.setPatientId(pt.getId());
				}

				//set


				Prescription.FillRequest prescriptionFill = new Prescription.FillRequest();

				prescriptionFill.setPharmacyID(p.getPharmacyID());

				//set date filled
//				LocalDateCodec date = new LocalDateCodec();
//				prescriptionFill.setDateFilled(String.valueOf(date));
//				p.setDateFilled(String.valueOf(date));

				prescriptionFill.setDateFilled(LocalDate.now().toString());

// Set date filled for p (assuming p is an instance of PrescriptionView)
				p.setDateFilled(LocalDate.now().toString());

				//Get drug quantity, drug name, and add them to prescription view
				//add calculate prescription cost and add it to view


				//Get drug and set drug's name in view
				Drug drug = drugRepository.findByName(prescription.getDrugName());
				p.setDrugName(drug.getName());
				//Drugcost x quantity
				ArrayList<Pharmacy.DrugCost> drugCosts = pharmacyRepository.findByNameAndAddress(p.getPharmacyName(), p.getPharmacyAddress()).getDrugCosts();
				double cost = -1;
				for (Pharmacy.DrugCost drugCost : drugCosts) {
					if(drugCost.getDrugName().equals(prescription.getDrugName())) cost = drugCost.getCost();
				}
				double prescriptionCost = prescription.getQuantity() * cost;
				p.setQuantity(prescription.getQuantity());
				prescriptionFill.setCost(String.valueOf(prescriptionCost));
				p.setCost(String.valueOf(prescriptionCost));
				p.setPharmacyID(pharmacyRepository.findByNameAndAddress(p.getPharmacyName(), p.getPharmacyAddress()).getId());
				p.setPharmacyPhone(pharmacyRepository.findByNameAndAddress(p.getPharmacyName(), p.getPharmacyAddress()).getPhone());
				//Add current rx fill to the fillRequests arrayList
				//Update fillRequests and put number of refills remaining in the view
				ArrayList<Prescription.FillRequest> fillRequests = prescription.getFills();
				fillRequests.add(prescriptionFill);
				prescription.setFills(fillRequests);
				p.setRefills(prescription.getRefills()- prescription.getFills().size());

				model.addAttribute("message", "Prescription filled.");
				model.addAttribute("prescription", p);
				return "prescription_show";


			}
		} else {
			//State that rx is not present bc rxid is not found
			model.addAttribute("message", "Rxid not found.");
			model.addAttribute("prescription", p);
			return "prescription_fill";
		}
	}


}
