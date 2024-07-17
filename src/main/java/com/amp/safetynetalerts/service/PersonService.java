package com.amp.safetynetalerts.service;

import com.amp.safetynetalerts.dto.*;
import com.amp.safetynetalerts.exception.PersonDeleteException;
import com.amp.safetynetalerts.exception.PersonUpdateException;
import com.amp.safetynetalerts.model.DataWrapper;
import com.amp.safetynetalerts.model.Person;


import java.io.IOException;
import java.util.*;

import com.amp.safetynetalerts.repository.DataWrapperRepository;
import lombok.Data;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.NoHandlerFoundException;


@Data
@Service
public class PersonService {

    public void addPerson(List<Person> persons, String firstName, String lastName, String address, String city, String zip, String phone, String email) {

        Person person = new Person();
        if (firstName != null) {
            person.setFirstName(firstName);
        }
        if (lastName != null) {
            person.setLastName(lastName);
        }
        if (address != null) {
            person.setAddress(address);
        }
        if (city != null) {
            person.setCity(city);
        }
        if (zip != null) {
            person.setZip(zip);
        }
        if (phone != null) {
            person.setPhone(phone);
        }
        if (email != null) {
            person.setEmail(email);
        }
        persons.add(person);
    }

    public void updatePerson(List<Person> persons, String firstName, String lastName, String address, String city, String zip, String phone, String email) {

        boolean personFound = false;

        for (Person person : persons) {
            if (person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)) {
                personFound = true;
                if (address != null) {
                    person.setAddress(address);
                }
                if (city != null) {
                    person.setCity(city);
                }
                if (zip != null) {
                    person.setZip(zip);
                }
                if (phone != null) {
                    person.setPhone(phone);
                }
                if (email != null) {
                    person.setEmail(email);
                }
                break;
            }
        }

        if (!personFound) {
            throw new PersonUpdateException("Person not found");
        }
    }

    public void deletePerson(List<Person> persons, String firstName, String lastName) {

        boolean isDeleted = persons.removeIf(p -> p.getFirstName().equals(firstName) && p.getLastName().equals(lastName));

        if (!isDeleted) {
            throw new PersonDeleteException("No person was deleted with the provided user details!");
        }
    }

    public static Person getPerson(List<Person> persons, String firstName, String lastName) throws NoHandlerFoundException {

        for (Person person : persons) {
            if (person.getFirstName().equals(firstName) && person.getLastName().equals(lastName)) {
                return person;
            }
        }
        throw new NoHandlerFoundException("GET", "/" + firstName + "/" + lastName, null);
    }

    public static PersonDTO fetchPerson(String firstName, String lastName) throws NoHandlerFoundException {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        return PersonMapper.toPersonDTO(getPerson(dataWrapper.getPersons(), firstName, lastName));
    }

    public PersonDTO addAndPersistPerson(Person person) throws IOException {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        addPerson(
                dataWrapper.getPersons(),
                person.getFirstName(),
                person.getLastName(),
                person.getAddress(),
                person.getCity(),
                person.getZip(),
                person.getPhone(),
                person.getEmail()
        );
        DataWrapperRepository.updateFileWithDataWrapper(dataWrapper);
        return PersonMapper.toPersonDTO(person);
    }

    public void updatePersonDataWrapper(Person person) throws IOException {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        updatePerson(dataWrapper.getPersons(),
                person.getFirstName(),
                person.getLastName(),
                person.getAddress(),
                person.getCity(),
                person.getZip(),
                person.getPhone(),
                person.getEmail());
        DataWrapperRepository.updateFileWithDataWrapper(dataWrapper);
    }

    public void deletePersonDataWrapper(String firstName, String lastName) throws IOException {

        DataWrapper dataWrapper = DataWrapperRepository.getDataWrapper();
        deletePerson(dataWrapper.getPersons(), firstName, lastName);
        DataWrapperRepository.updateFileWithDataWrapper(dataWrapper);

    }
}