CREATE OR REPLACE PROCEDURE calculate_profit
  (itemid IN NUMBER, profit OUT NUMBER)
IS
  BEGIN

    SELECT sum(DOCUMENTS_ITEMS.PRICE * QUANTITY * TRANSACTION_SIGN * (-1))
    INTO profit
    FROM DOCUMENTS_ITEMS
      JOIN ITEMS USING (item_id)
    WHERE item_id = itemid
    GROUP BY ITEM_ID, i_name;

    EXCEPTION
    WHEN OTHERS
    THEN
      profit := 0;
  END;
/
