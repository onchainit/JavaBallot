pragma solidity ^0.4.10;
contract ReportBallot {

    struct Proposal {
        uint voteCount;
    }

    address public first_beneficiary;
    address public second_beneficiary;
    uint8 first_beneficiary_rate;
    uint8 second_beneficiary_rate;
    uint256 public collected_cap;
    uint256 public cap;
        
    Proposal[] public proposals;
    mapping (address => uint) pendingWithdrawals;


    modifier onlyBeneficiaries() { // Modifier
        require(msg.sender == first_beneficiary || msg.sender == second_beneficiary);
        _;
    }

    modifier onlyWithPendingWithDraw() { // Modifier
        require(pendingWithdrawals[msg.sender] > 0);
        _;
    }
    
    modifier onlyUnderCap() { // Modifier
        require(collected_cap < cap);
        _;
    }
    
    modifier onlyOverCap() { // Modifier
        require(collected_cap >= cap);
        _;
    }

    event CapReached(uint amount); 

    /// Create a new ballot with $(_numProposals) different proposals.
    function ReportBallot(uint8 _numProposals, address _first_beneficiary, uint8 _first_beneficiary_rate, uint256 _cap) {
        first_beneficiary = _first_beneficiary;
        second_beneficiary = msg.sender;
        first_beneficiary_rate = _first_beneficiary_rate;
        if (first_beneficiary_rate >= 100) {
            first_beneficiary_rate = 50;    
        } 
        second_beneficiary_rate = 100 - first_beneficiary_rate;
        cap = _cap ;
        proposals.length = _numProposals;
    }



    /// Give a single vote to proposal $(proposal).
    function vote(uint8 proposal) payable onlyUnderCap {
        require(proposal < proposals.length);
        collected_cap += msg.value;
        pendingWithdrawals[first_beneficiary] += (msg.value / 100) * first_beneficiary_rate;
        pendingWithdrawals[second_beneficiary] += (msg.value / 100) * second_beneficiary_rate;
        proposals[proposal].voteCount += msg.value;
        if (collected_cap >= cap) {
            CapReached(collected_cap);
        } 
    }

    function winningProposal() constant returns (uint8 winProposal) {
        uint256 winningVoteCount = 0;
        for (uint8 proposal = 0; proposal < proposals.length; proposal++)
            if (proposals[proposal].voteCount > winningVoteCount) {
                winningVoteCount = proposals[proposal].voteCount;
                winProposal = proposal;
            }

    }
    
    
    function withdraw() onlyBeneficiaries onlyWithPendingWithDraw {
        uint amount = pendingWithdrawals[msg.sender];
        // Remember to zero the pending refund before
        // sending to prevent re-entrancy attacks
        pendingWithdrawals[msg.sender] = 0;
        msg.sender.transfer(amount);
    }
    
    function abort() onlyBeneficiaries {
        //refund voters...
        collected_cap = 0;
        uint orig_length = proposals.length;
        proposals.length = 0;
        proposals.length = orig_length;
    }

    function sumCap() returns (uint summedCap) {
        summedCap = collected_cap + 100;
        return summedCap;
    }

    function setCap(uint _cap) onlyBeneficiaries {
        cap = _cap;
    }
}
