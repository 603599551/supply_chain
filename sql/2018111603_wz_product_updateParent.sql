UPDATE s_product p1, s_product p2 set p1.parent_name=p2.name where p1.parent_id=p2.id;
UPDATE s_product p1, s_product p2 set p1.parent_num=p2.num where p1.parent_id=p2.id;