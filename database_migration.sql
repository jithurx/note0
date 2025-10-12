-- Add approval_status column to materials table
-- This script should be run to update the existing database schema

-- Add the approval_status column with default value 'PENDING'
ALTER TABLE materials ADD COLUMN IF NOT EXISTS approval_status VARCHAR(20) DEFAULT 'PENDING';

-- Update existing materials to be approved (since they were already uploaded before this feature)
UPDATE materials SET approval_status = 'APPROVED' WHERE approval_status IS NULL;

-- Create index for better performance on approval status queries
CREATE INDEX IF NOT EXISTS idx_materials_approval_status ON materials(approval_status);

-- Ensure the admin user exists with ADMIN role
-- This will be handled by the Java code, but here's the SQL for reference:
-- INSERT INTO users (full_name, email, password_hash, role, is_active, is_verified, college_name, semester) 
-- VALUES ('Admin User', 'aswin@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'ADMIN', true, true, 'Admin College', 1)
-- ON CONFLICT (email) DO UPDATE SET role = 'ADMIN';
