ALTER TABLE "public"."contact_warehouse_pair" ADD COLUMN "hash" text NOT NULL default '--';
ALTER TABLE "public"."contact_warehouse_pair" ADD UNIQUE ("contact_person_id");