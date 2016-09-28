insert into t_fxr_jobs
select a.id, nvl((select '1' from t_rem_conditions c where c.id = a.id and code='SQL'),'0') adm, a.name, a.parent_id from t_rem_jobs a;

insert into t_fxr_seq
values('JOBS',(select max(id)+1 from t_fxr_jobs));