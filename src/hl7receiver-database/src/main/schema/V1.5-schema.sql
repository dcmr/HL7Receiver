/*
	Schema V1.5: Add SIU messages to dictionary
*/

insert into dictionary.message_type (message_type, description) values
('SIU^S12', 'Notification of new appointment booking'),
('SIU^S13', 'Notification of appointment rescheduling'),
('SIU^S14', 'Notification of appointment modification'),
('SIU^S15', 'Notification of appointment cancellation'),
('SIU^S16', 'Notification of appointment discontinuation'),
('SIU^S17', 'Notification of appointment deletion'),
('SIU^S18', 'Notification of addition of service/resource on appointment'),
('SIU^S19', 'Notification of modification of service/resource on appointment'),
('SIU^S20', 'Notification of cancellation of service/resource on appointment'),
('SIU^S21', 'Notification of discontinuation of service/resource on appointment'),
('SIU^S22', 'Notification of deletion of service/resource on appointment'),
('SIU^S23', 'Notification of blocked schedule time slot(s)'),
('SIU^S24', 'Notification of opened ("un-blocked") schedule time slot(s)'),
('SIU^S26', 'Notification that patient did not show up for scheduled appointment'),
('ACK^S12', 'Acknowledgement to notification of new appointment booking'),
('ACK^S13', 'Acknowledgement to notification of appointment rescheduling'),
('ACK^S14', 'Acknowledgement to notification of appointment modification'),
('ACK^S15', 'Acknowledgement to notification of appointment cancellation'),
('ACK^S16', 'Acknowledgement to notification of appointment discontinuation'),
('ACK^S17', 'Acknowledgement to notification of appointment deletion'),
('ACK^S18', 'Acknowledgement to notification of addition of service/resource on appointment'),
('ACK^S19', 'Acknowledgement to notification of modification of service/resource on appointment'),
('ACK^S20', 'Acknowledgement to notification of cancellation of service/resource on appointment'),
('ACK^S21', 'Acknowledgement to notification of discontinuation of service/resource on appointment'),
('ACK^S22', 'Acknowledgement to notification of deletion of service/resource on appointment'),
('ACK^S23', 'Acknowledgement to notification of blocked schedule time slot(s)'),
('ACK^S24', 'Acknowledgement to notification of opened ("un-blocked") schedule time slot(s)'),
('ACK^S26', 'Acknowledgement to notification that patient did not show up for scheduled appointment');