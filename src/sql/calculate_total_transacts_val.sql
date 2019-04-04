CREATE OR REPLACE PROCEDURE calculate_total_transacts_val
  (contractorid IN NUMBER, VALUE OUT NUMBER)
IS
  BEGIN
    SELECT SUM(price * quantity)
    INTO VALUE
    FROM documents
      JOIN documents_items USING (document_id)
    WHERE contractor_id = contractorid;

    IF VALUE IS NULL
    THEN VALUE := 0;
    END IF;

    EXCEPTION
    WHEN OTHERS
    THEN
      VALUE := 0;
  END;
/
