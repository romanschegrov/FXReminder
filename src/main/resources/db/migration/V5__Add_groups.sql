insert into t_fxr_group
select rownum, a.code, a.descr
  from t_rem_groups a;

insert into t_fxr_seq
values('GROUPS',(select max(id)+1 from t_fxr_group));