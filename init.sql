-- CONCEPT TABLE
CREATE TABLE concept (
                         uuid UUID PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         concept_class_name VARCHAR(255),
                         concept_class_uuid UUID,
                         concept_class_description TEXT,
                         descriptions_uuid UUID,
                         descriptions_description TEXT,
                         datatype_uuid UUID,
                         version VARCHAR(50)
);

-- DRUG TABLE
CREATE TABLE drug (
                      uuid UUID PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      strength VARCHAR(50),
                      maximum_daily_dose VARCHAR(50),
                      minimum_daily_dose VARCHAR(50),
                      retired BOOLEAN,
                      concept_uuid UUID REFERENCES concept(uuid) ON DELETE CASCADE,
                      combination BOOLEAN,
                      dosage_form VARCHAR(100)
);

-- PATIENT TABLE
CREATE TABLE patient (
                         uuid UUID PRIMARY KEY,
                         display VARCHAR(255),
                         person_name VARCHAR(255),
                         gender CHAR(1),
                         age INT,
                         birthdate DATE
);

-- PATIENT IDENTIFIER TYPE TABLE
CREATE TABLE patient_identifier_type (
                                         uuid UUID PRIMARY KEY,
                                         name VARCHAR(255) NOT NULL,
                                         description TEXT,
                                         format VARCHAR(100),
                                         format_description TEXT,
                                         required BOOLEAN,
                                         validator TEXT
);

-- PERSON TABLE
CREATE TABLE person (
                        uuid UUID PRIMARY KEY,
                        display VARCHAR(255) NOT NULL,
                        gender CHAR(1),
                        age INT,
                        birthdate DATE
);

-- PROGRAM TABLE
CREATE TABLE program (
                         uuid UUID PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         concept_name_uuid UUID REFERENCES concept(uuid) ON DELETE CASCADE,
                         concept_name VARCHAR(255),
                         concept_description TEXT,
                         outcomes_concept_name VARCHAR(255),
                         outcomes_concept_uuid UUID REFERENCES concept(uuid) ON DELETE CASCADE,
                         outcomes_concept_description TEXT
);

-- RELATIONSHIP TYPE TABLE
CREATE TABLE relationship_type (
                                   uuid UUID PRIMARY KEY,
                                   description TEXT,
                                   a_is_to_b VARCHAR(100),
                                   b_is_to_a VARCHAR(100),
                                   weight INT,
                                   display VARCHAR(255)
);

-- VISIT TYPE TABLE
CREATE TABLE visit_type (
                            uuid UUID PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            description TEXT,
                            retired BOOLEAN
);

-- VISIT TABLE
CREATE TABLE visit (
                       uuid UUID PRIMARY KEY,
                       display VARCHAR(255),
                       patient_uuid UUID REFERENCES patient(uuid) ON DELETE CASCADE,
                       patient_display VARCHAR(255),
                       visit_type_uuid UUID REFERENCES visit_type(uuid) ON DELETE CASCADE,
                       visit_type_display VARCHAR(255),
                       visit_location_uuid UUID,
                       visit_location_display VARCHAR(255),
                       start_datetime TIMESTAMP,
                       stop_datetime TIMESTAMP
);

-- OBS TABLE
CREATE TABLE obs (
                     uuid UUID PRIMARY KEY,
                     display VARCHAR(255),
                     patient_uuid UUID REFERENCES patient(uuid) ON DELETE CASCADE,
                     concept_uuid UUID REFERENCES concept(uuid) ON DELETE CASCADE,
                     concept_name VARCHAR(255),
                     value_uuid UUID,
                     obs_datetime TIMESTAMP
);

-- ENCOUNTER TABLE (für die List<UUID> in VisitDTO)
CREATE TABLE encounter (
                           uuid UUID PRIMARY KEY,
                           visit_uuid UUID REFERENCES visit(uuid) ON DELETE CASCADE,
                           display VARCHAR(255)
);
