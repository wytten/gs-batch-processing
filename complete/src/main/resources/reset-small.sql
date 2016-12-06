set timing on

delete from sot22.drw_pay_eob;
commit;

insert into sot22.drw_pay_eob
select * from sot22.pay_eob
where rownum <= 10000;
commit;

select count(*) from sot22.drw_pay_eob;
