insert into t_fxr_conditions
select rownum, c.code, c.value, c.id from t_rem_conditions c;

insert into t_fxr_conditions
select rownum + (select count(*) from t_fxr_conditions) id,
       'SCHEDULE' code,
       nvl((select '1' from t_fxr_conditions c where c.code='TIMER' and c.job_id=j.id),'0') value,
       id job_id
from t_fxr_jobs j where j.job='1';

insert into t_fxr_seq
values('CONDITIONS',(select max(id)+1 from t_fxr_conditions));

update t_rem_conditions set code = 'NOTIFY' where code = 'SHOW';

update t_rem_conditions set code = 'AVAILABLE' where code = 'ACCESS';