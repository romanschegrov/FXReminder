insert into t_fxr_ug
select u.id,g.id from t_rem_ug ug, t_fxr_users u, t_fxr_group g
where 1=1
  and u.code = ug.puser
  and g.code = ug.pgroup;