CREATE OR REPLACE TRIGGER msg_notif_trigger
  AFTER INSERT
  ON messages
  FOR EACH ROW
  DECLARE
    notification_exists NUMBER;
  BEGIN

    SELECT count(*)
    INTO notification_exists
    FROM messages_notifications
    WHERE sender_id = :new.sender_id;

    dbms_output.put_line('sender_id: ' || to_char(:new.sender_id));
    dbms_output.put_line('recipient_id: ' || to_char(:new.recipient_id));
    dbms_output.put_line('msg: ' || :new.message);
    dbms_output.put_line('msg: ' || to_char(NOTIFICATION_EXISTS));

    IF notification_exists = 0
    THEN
      dbms_output.put_line('INSERT');
      INSERT INTO messages_notifications (sender_id, recipient_id, ack) VALUES (:new.sender_id, :new.recipient_id, 'N');
    ELSE
      dbms_output.put_line('UPDATE');
      UPDATE messages_notifications
      SET ack = 'N'
      WHERE sender_id = :new.sender_id AND recipient_id = :new.recipient_id;
    END IF;

    --INSERT INTO messages_notifications (sender_id, recipient_id, ack) VALUES (:new.sender_id, :new.recipient_id, 'N');
  END;