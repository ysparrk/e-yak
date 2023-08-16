insert into medicine_routine(routine) values
  ('BED_AFTER'),
  ('BED_BEFORE'),
  ('BREAKFAST_AFTER'),
  ('BREAKFAST_BEFORE'),
  ('DINNER_AFTER'),
  ('DINNER_BEFORE'),
  ('LUNCH_AFTER'),
  ('LUNCH_BEFORE')^;

insert into choice_status_entity(choice_status) values
    ('NO_SYMPTOMS'),
    ('HEADACHE'),
    ('ABDOMINAL_PAIN'),
    ('NAUSEA'),
    ('VOMITING'),
    ('FEVER'),
    ('DIARRHEA'),
    ('INDIGESTION'),
    ('COUGH')^;

create or replace procedure load_survey_contents()
begin
    declare survey_id bigint;
    declare cur datetime;
    set cur = now();
    insert into survey(date, created_at, updated_at) values (CURDATE(), cur, cur);
    set survey_id = last_insert_id();
    insert into survey_content(survey_id, survey_content_type) values(survey_id, "CHOICE_STATUS");
    insert into survey_content(survey_id, survey_content_type) values(survey_id, "CHOICE_EMOTION");
    insert into survey_content(survey_id, survey_content_type) values(survey_id, "TEXT");
end ^;

call load_survey_contents ^;