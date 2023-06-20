import React from 'react';

class SeatBooking extends React.Component {
  constructor(props) {
    super(props);
    
    this.state = {
      seats: this.generateSeats(),
    };
  }
  
  generateSeats() {
    const rows = 10;
    const columns = 8;
    const totalSeats = rows * columns;
    const seats = [];
    
    for (let i = 1; i <= totalSeats; i++) {
      const row = Math.floor((i - 1) / columns) + 1;
      const column = ((i - 1) % columns) + 1;
      const isWalkway = column === 4 || column === 5;
      const seat = {
        number: i,
        row,
        column,
        available: !isWalkway,
        selected: false,
      };
      
      seats.push(seat);
    }
    
    return seats;
  }
  
  handleSeatClick(seat) {
    if (seat.available) {
      const updatedSeats = this.state.seats.map((s) => {
        if (s.number === seat.number) {
          return { ...s, selected: !s.selected };
        }
        return s;
      });
      
      this.setState({ seats: updatedSeats });
    }
  }
  
  render() {
    return (
      <div className="container">
        <h1>Movie Seat Booking</h1>
        <div className="screen">Screen</div>
        <div className="seats">
          {this.state.seats.map((seat) => (
            <div
              key={seat.number}
              className={`seat ${seat.available ? 'available' : 'unavailable'} ${seat.selected ? 'selected' : ''} ${seat.column === 4 || seat.column === 5 ? 'walkway' : ''}`}
              onClick={() => this.handleSeatClick(seat)}
            >
              <span className="seat-number">{seat.number}</span>
            </div>
          ))}
        </div>
        <p>Available seats are green, unavailable seats are gray.</p>
        <button>Book Selected Seats</button>
      </div>
    );
  }
}

export default SeatBooking;
