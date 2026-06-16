package com.leadestate.backend.repository;

import com.leadestate.backend.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    @Query(value = """
        SELECT 
        l.name,
        p.name,
        fu.followup_date,
        fu.status
        FROM reminders r
        JOIN follow_ups fu ON r.followup_id = fu.id
        JOIN leads l ON fu.lead_id = l.id
        LEFT JOIN properties p ON l.property_id = p.id
        WHERE fu.status = 'pending'
        ORDER BY fu.followup_date ASC
    """, nativeQuery = true)
    List<Object[]> getTodayReminders();

    @Query(value = """
        SELECT
            r.id,
            r.reminder_date,
            fu.status,

            l.id AS lead_id,
            l.name,
            l.phone,
            l.source,

            p.name AS property_name,

            u.name AS sales_name

        FROM reminders r

        JOIN follow_ups fu
            ON r.followup_id = fu.id

        JOIN leads l
            ON fu.lead_id = l.id

        LEFT JOIN properties p
            ON l.property_id = p.id

        LEFT JOIN users u
            ON l.sales_id = u.id

        WHERE fu.status != 'cancelled' 
        AND fu.status != 'deleted'

        ORDER BY fu.created_at DESC
    """, nativeQuery = true)
    List<Object[]> getAllReminderDetails();

    @Query(value = """
        SELECT
            r.id,
            r.reminder_date,
            fu.status,

            l.id AS lead_id,
            l.name,
            l.phone,
            l.source,

            p.name AS property_name,

            u.name AS sales_name

        FROM reminders r

        JOIN follow_ups fu
            ON r.followup_id = fu.id

        JOIN leads l
            ON fu.lead_id = l.id

        LEFT JOIN properties p
            ON l.property_id = p.id

        LEFT JOIN users u
            ON l.sales_id = u.id

        WHERE fu.status != 'cancelled'
        AND fu.status != 'deleted'
        AND l.sales_id = :salesId

        ORDER BY fu.created_at DESC
        """, nativeQuery = true)
        List<Object[]> getReminderDetailsBySalesId(@Param("salesId") Integer salesId);

    @Query(value = """
        SELECT l.sales_id
        FROM reminders r
        JOIN follow_ups fu
            ON r.followup_id = fu.id
        JOIN leads l
            ON fu.lead_id = l.id
        WHERE r.id = :id
    """, nativeQuery = true)
    Integer getSalesIdByReminderId(
        @Param("id") Long id
    );

    @Query(value = """
        SELECT
            l.name,
            p.name,
            fu.followup_date,
            fu.status
        FROM reminders r
        JOIN follow_ups fu
            ON r.followup_id = fu.id
        JOIN leads l
            ON fu.lead_id = l.id
        LEFT JOIN properties p
            ON l.property_id = p.id
        WHERE fu.status = 'pending'
        AND l.sales_id = :salesId
        ORDER BY fu.followup_date ASC
    """, nativeQuery = true)
    List<Object[]> getTodayRemindersBySales(
            @Param("salesId") Integer salesId
    );
}