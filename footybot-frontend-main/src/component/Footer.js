import React from 'react';
import { Link } from 'react-router-dom';
import { FaTwitter, FaFacebook, FaInstagram, FaYoutube } from 'react-icons/fa';
import './Footer.css';

const Footer = () => {
  return (
    <footer className="app-footer">
      <div className="footer-container">
        {/* Main Footer Content */}
        <div className="footer-main">
          <div className="footer-section">
            <div className="footer-logo">
              <div className="footer-logo-icon">⚽</div>
              <h3>FutQuiz</h3>
            </div>
            <p className="footer-description">
              Your ultimate destination for football statistics, match results, 
              head-to-head comparisons, and interactive quizzes. Stay updated 
              with the latest Premier League action!
            </p>
          </div>

          <div className="footer-section">
            <h4>Quick Links</h4>
            <ul className="footer-links">
              <li><Link to="/">Standings</Link></li>
              <li><Link to="/results">Match Results</Link></li>
              <li><Link to="/h2h">H2H Stats</Link></li>
              <li><Link to="/quiz">Quiz</Link></li>
              <li><Link to="/leaderboard">Leaderboard</Link></li>
            </ul>
          </div>

          <div className="footer-section">
            <h4>Features</h4>
            <ul className="footer-links">
              <li>Live Match Data</li>
              <li>Team Statistics</li>
              <li>Player Information</li>
              <li>Interactive Quizzes</li>
              <li>Leaderboards</li>
            </ul>
          </div>

          <div className="footer-section">
            <h4>Connect</h4>
            <div className="social-links">
              <a href="https://x.com/PremierLeague" className="social-link" aria-label="Twitter">
                <FaTwitter className="social-icon" />
              </a>
              <a href="https://www.facebook.com/premierleague" className="social-link" aria-label="Facebook">
                <FaFacebook className="social-icon" />
              </a>
              <a href="https://instagram.com/PremierLeague" className="social-link" aria-label="Instagram">
                <FaInstagram className="social-icon" />
              </a>
              <a href="https://www.youtube.com/PremierLeague" className="social-link" aria-label="Youtube">
                <FaYoutube className="social-icon" />
              </a>
            </div>
            {/* <p className="contact-info">
              📧 contact@futquiz.com<br />
              📱 +1 (555) 123-4567
            </p> */}
          </div>
        </div>

        {/* Footer Bottom */}
        <div className="footer-bottom">
          <div className="footer-bottom-content">
            <p>&copy; 2025 FutQuiz. All rights reserved.</p>
            <div className="footer-legal">
              <a href="#" className="legal-link">Privacy Policy</a>
              <a href="#" className="legal-link">Terms of Service</a>
              <a href="#" className="legal-link">Cookie Policy</a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;