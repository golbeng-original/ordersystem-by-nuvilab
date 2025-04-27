import { Box, Button, Card, CardContent, List, ListItem, ListItemText, TextField, Typography } from "@mui/material";
import { useState } from "react";
import useAddAddresses from "../../features/user/hooks/useAddAddresses";
import { useNavigate } from "react-router-dom";

interface AddressParam {
    ownerName: string;
    address: string;
    phoneNumber: string;
}

const AddresssInputArea = (props:{
    onHandleAddNewAddress: (params:AddressParam) => void
}) => {

    const { onHandleAddNewAddress } = props;


    const [name, setName] = useState("");
    const [address, setAddress] = useState("");
    const [phone, setPhone] = useState("");

    const handleAddOrderer = () => {
        if (!name || !address || !phone) {
          alert("모든 항목을 입력해 주세요.");
          return;
        }

        onHandleAddNewAddress({
            ownerName: name,
            address: address,
            phoneNumber: phone
        });
    
        setName("");
        setAddress("");
        setPhone("");
    };

    return (
        <Card>
            <CardContent>
                <Typography variant="h5" gutterBottom>
                    주문자 정보 입력
                </Typography>
                <Box display="flex" flexDirection="column" gap={2}>
                    <TextField
                        label="주문자 이름"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        fullWidth
                    />
                    <TextField
                        label="배송지"
                        value={address}
                        onChange={(e) => setAddress(e.target.value)}
                        fullWidth
                    />
                    <TextField
                        label="전화번호"
                        value={phone}
                        onChange={(e) => setPhone(e.target.value)}
                        fullWidth
                    />
                    <Button variant="contained" color="primary" onClick={handleAddOrderer}>
                        추가
                    </Button>
                </Box>
            </CardContent>
        </Card>
    );
}

const MyAddressListPage = () => {

    const navigate = useNavigate();
    const { addresses, requestAddress } = useAddAddresses();

    const onHandleAddNewAddress = async (params:AddressParam) => {

        await requestAddress({
            name: params.ownerName,
            address: params.address,
            phoneNumber: params.phoneNumber
        })
    };

    const onHandleGoHome = () => {
        navigate('/stores');
    }

    return (
        <Box sx={{ 
            background: 'linear-gradient(to right, #667eea, #764ba2)',
            width: '100vw',
            height: '100vh',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            flexDirection: 'column',
        }}>
            <AddresssInputArea onHandleAddNewAddress={onHandleAddNewAddress}/>

            <Box mt={4}>
                <Typography variant="h6">주소 리스트</Typography>
                <List>
                    {addresses.map((address, index) => (
                        <ListItem key={index} divider>
                            <ListItemText
                              primary={`${address.ownerName} (${address.phoneNumber})`}
                              secondary={`배송지: ${address.address}`}
                            />
                        </ListItem>
                    ))}
                </List>
            </Box>
            <Button variant="contained" color="primary" sx={{ mt: 3 }} onClick={onHandleGoHome}>
                홈으로 돌아가기
            </Button>
        </Box>
    );
}

export default MyAddressListPage;