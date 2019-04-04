sqlplus -L -S $1 @create_user.sql &&
script_dir=$(pwd) &&
tmp_filename=$script_dir"/tmp.sql" &&
echo $tmp_filename &&
cd ../src/sql &&
cat create_schema.ddl init_tables.sql calculate_item_profit.sql calculate_total_transacts_val.sql calculate_item_revenue.sql > $tmp_filename &&
echo "exit" >> $tmp_filename &&
cd $script_dir &&
sqlplus -L -S magazynier_demo/magazynier_demo_123 @tmp.sql &&
rm $tmp_filename &&
echo "OK"
