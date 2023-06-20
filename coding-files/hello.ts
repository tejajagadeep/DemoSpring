// seat-booking.component.ts
import { Component } from '@angular/core';

@Component({
  selector: 'app-seat-booking',
  templateUrl: './seat-booking.component.html',
  styleUrls: ['./seat-booking.component.css']
})
export class SeatBookingComponent {
  rows: any[][];

  constructor() {
    this.rows = [];

    for (let i = 0; i < 10; i++) {
      const row: any[] = [];
      for (let j = 0; j < 8; j++) {
        const seatNumber = i * 8 + j + 1;
        const seat = {
          number: seatNumber,
          available: true,
          selected: false
        };
        row.push(seat);
      }
      this.rows.push(row);
    }
  }

  bookSelectedSeats() {
    // Logic to book selected seats
    // Add your implementation here
  }
}
