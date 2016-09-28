insert into t_fxr_users
select rownum+1, 0, a.code, a.descr
  from t_rem_users a where a.code!='DKB';

insert into t_fxr_seq
values('USERS',(select max(id)+1 from t_fxr_users));