--drop trigger msg_notif_trigger;

CREATE OR REPLACE TRIGGER msg_notif_trigger
  AFTER INSERT
  ON messages
  FOR EACH ROW
  DECLARE
    notification_exists     NUMBER;
    notification_ack_status VARCHAR2(1);
  BEGIN

    SELECT count(*)
    INTO notification_exists
    FROM messages_notifications
    WHERE sender_id = :new.sender_id AND recipient_id = :new.recipient_id /*AND :new.sender_id != :new.recipient_id*/;

    IF notification_exists = 0
    THEN
      INSERT INTO messages_notifications (sender_id, recipient_id, ack) VALUES (:new.sender_id, :new.recipient_id, 'N');
    ELSE
      SELECT ack
      INTO notification_ack_status
      FROM messages_notifications
      WHERE sender_id = :new.sender_id AND recipient_id = :new.recipient_id;

      IF notification_ack_status != 'N'
      THEN
        UPDATE messages_notifications
        SET ack = 'N'
        WHERE sender_id = :new.sender_id AND recipient_id = :new.recipient_id;
      END IF;
    END IF;
  END;
