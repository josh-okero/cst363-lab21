// Insert a doctor document
db.doctor.insert({
    _id: 6,
    lastName: 'wisneski',
    firstName: 'david',
    specialty: 'family',
    practice_since_year: '2006',
    ssn: '123121234',
    _class: 'lab21.model.Doctor'
});

// Insert a patient document
db.patient.insert({
    _id: 1,
    lastName: 'cat',
    firstName: 'socks',
    birthdate: '2018-12-01',
    ssn: '999887777',
    street: '123 second street',
    city: 'marina',
    state: 'ca',
    zipcode: '93933',
    primaryName: 'wisneski',
    _class: 'lab21.model.Patient'
});

// Insert a prescription document
db.prescription.insert({
    _id: 1,
    drugName: 'lisinopril',
    quantity: 100,
    patient_id: 1,
    doctor_id: 6,
    dateCreated: '2023-11-09',
    refills: 1,
    fills: [],
    _class: 'lab21.model.Prescription'
});

// Insert a pharmacy document
db.pharmacy.insert({
    _id: 1,
    name: 'cvs',
    address: '123 main',
    phone: '813-774-1200',
    drugCosts: [{ drugName: 'lisinopril', cost: 7.5 }]
});

// Insert a drug document
db.drug.insert({
    _id: 1,
    name: 'lisinopril'
});
