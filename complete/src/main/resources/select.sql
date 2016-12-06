select eob_id, patient_first_name, patient_last_name from sot22.drw_pay_eob
where upper(patient_first_name) != patient_first_name
--or eob_id = 70927 -- Wei Qian
order by eob_id
