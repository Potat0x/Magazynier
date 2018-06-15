CREATE OR REPLACE PROCEDURE calculate_revenue
  (itemid IN NUMBER, revenue OUT NUMBER)
IS
  BEGIN

    SELECT sum(DOCUMENTS_ITEMS.PRICE * QUANTITY)
    INTO revenue
    FROM DOCUMENTS_ITEMS
      JOIN ITEMS USING (item_id)
    WHERE item_id = itemid AND TRANSACTION_SIGN = -1
    GROUP BY ITEM_ID, i_name;

    EXCEPTION
    WHEN OTHERS
    THEN
      revenue := 0;
  END;

